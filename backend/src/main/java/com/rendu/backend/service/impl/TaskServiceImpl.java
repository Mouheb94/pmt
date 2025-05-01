package com.rendu.backend.service.impl;

import com.rendu.backend.dao.ProjectMemberRepository;
import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.TaskRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.*;

import com.rendu.backend.service.EmailService;
import com.rendu.backend.service.TaskHistoryService;
import com.rendu.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
        @Autowired
        private final EmailService emailService = new EmailService();
        private final TaskRepository taskRepository;
        private final UserRepository userRepository;
        private final ProjectMemberRepository projectMemberRepository;
        private final ProjectRepository projectRepository;
    private final TaskHistoryService taskHistoryService;

        @Autowired
        public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, TaskHistoryService taskHistoryService) {
            this.taskRepository = taskRepository;
            this.userRepository = userRepository;
            this.projectMemberRepository = projectMemberRepository;
            this.projectRepository = projectRepository;
            this.taskHistoryService = taskHistoryService;
        }

        @Override
        public Task createTask(Task task,Long projectId) {
            Optional<Project> project = projectRepository.findById(projectId);
            task.setProject(project.get());
            project.get().getTasks().add(task);
            return taskRepository.save(task);
        }

        @Override
        public Task updateTask(Long id, Long modifiedById, Task updatedTask) {
            Optional<Task> optionalTask = taskRepository.findById(id);
            Optional<User> modifiedBy = userRepository.findById(modifiedById);
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();

                // Vérifier les changements champ par champ et loguer l’historique
                if (!task.getName().equals(updatedTask.getName())) {
                    logTaskChange(task, "name", task.getName(), updatedTask.getName(), modifiedBy.get());
                    task.setName(updatedTask.getName());
                }

                if (!task.getDescription().equals(updatedTask.getDescription())) {
                    logTaskChange(task, "description", task.getDescription(), updatedTask.getDescription(), modifiedBy.get());
                    task.setDescription(updatedTask.getDescription());
                }

                if (!task.getDueDate().equals(updatedTask.getDueDate())) {
                    logTaskChange(task, "dueDate", task.getDueDate().toString(), updatedTask.getDueDate().toString(), modifiedBy.get());
                    task.setDueDate(updatedTask.getDueDate());
                }

                if (task.getEndDate() != null && updatedTask.getEndDate() != null &&
                        !task.getEndDate().equals(updatedTask.getEndDate())) {
                    logTaskChange(task, "endDate", task.getEndDate().toString(), updatedTask.getEndDate().toString(), modifiedBy.get());
                    task.setEndDate(updatedTask.getEndDate());
                }

                if (!task.getPriority().equals(updatedTask.getPriority())) {
                    logTaskChange(task, "priority", task.getPriority().name(), updatedTask.getPriority().name(), modifiedBy.get());
                    task.setPriority(updatedTask.getPriority());
                }

                if (!task.getStatus().equals(updatedTask.getStatus())) {
                    logTaskChange(task, "status", task.getStatus().name(), updatedTask.getStatus().name(), modifiedBy.get());
                    task.setStatus(updatedTask.getStatus());
                }



                return taskRepository.save(task);
            } else {
                throw new RuntimeException("Task not found");
            }
        }
    private void logTaskChange(Task task, String field, String oldValue, String newValue, User modifiedBy) {
        taskHistoryService.logTaskChange(task, field, oldValue, newValue, modifiedBy);
    }


        @Override
        public Task getTaskById(Long id) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
        }

        @Override
        public List<Task> getAllTasks() {
            return taskRepository.findAll();
        }

        @Override
        public void deleteTask(Long id) {
            taskRepository.deleteById(id);
        }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.getTasksByStatus(status);
    }

    @Override
    public Task assigneTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        task.setAssignedTo(user);
        taskRepository.save(task);
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectId(task.getProject().getId());
        for (ProjectMember member : projectMembers) {
            String subject = "Nouvelle tâche assignée dans le projet " + task.getProject().getName();
            String body = "Bonjour " + member.getUser().getUsername() + ",\n\n" +
                    "La tâche '" + task.getName() + "' a été assignée à " + user.getUsername() + ".\n\n" +
                    "Merci de consulter le tableau de bord pour plus de détails.";

            emailService.sendEmail(member.getUser().getEmail(), subject, body);
        }

        return task;
    }

    @Override
    public List<Task> getAllTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }


}
