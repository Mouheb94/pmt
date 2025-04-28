package com.rendu.backend.service.impl;

import com.rendu.backend.dao.ProjectMemberRepository;
import com.rendu.backend.dao.ProjectRepository;
import com.rendu.backend.dao.TaskRepository;
import com.rendu.backend.dao.UserRepository;
import com.rendu.backend.enums.TaskStatus;
import com.rendu.backend.models.Project;
import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.models.Task;
import com.rendu.backend.models.User;

import com.rendu.backend.service.EmailService;
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

        @Autowired
        public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository) {
            this.taskRepository = taskRepository;
            this.userRepository = userRepository;
            this.projectMemberRepository = projectMemberRepository;
            this.projectRepository = projectRepository;
        }

        @Override
        public Task createTask(Task task,Long projectId) {
            Optional<Project> project = projectRepository.findById(projectId);
            task.setProject(project.get());
            project.get().getTasks().add(task);
            return taskRepository.save(task);
        }

        @Override
        public Task updateTask(Long id, Task updatedTask) {
            Optional<Task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();
                task.setName(updatedTask.getName());
                task.setDescription(updatedTask.getDescription());
                task.setDueDate(updatedTask.getDueDate());
                task.setEndDate(updatedTask.getEndDate());
                task.setPriority(updatedTask.getPriority());
                task.setStatus(updatedTask.getStatus());
                task.setAssignedTo(updatedTask.getAssignedTo());
                return taskRepository.save(task);
            } else {
                throw new RuntimeException("Task not found");
            }
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
