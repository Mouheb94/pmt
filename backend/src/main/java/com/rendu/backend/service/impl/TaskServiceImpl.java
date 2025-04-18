package com.rendu.backend.service.impl;

import com.rendu.backend.dao.TaskRepository;
import com.rendu.backend.models.Task;
import com.rendu.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {


        private final TaskRepository taskRepository;

        @Autowired
        public TaskServiceImpl(TaskRepository taskRepository) {
            this.taskRepository = taskRepository;
        }

        @Override
        public Task createTask(Task task) {
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

}
