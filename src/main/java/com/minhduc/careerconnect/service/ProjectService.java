package com.minhduc.careerconnect.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.ProjectForm;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProjectRepository;
import com.minhduc.careerconnect.repository.ProjectRepository.Project;

/**
 * Dự án cá nhân trong hồ sơ (task T4E796C).
 * Tối đa 20 dự án mỗi hồ sơ; sửa/xoá dự án của người khác -> 403.
 */
@Service
public class ProjectService {

    public static final int MAX_PROJECTS = 20;

    private final ProjectRepository projectRepository;
    private final ProfileRepository profileRepository;

    public ProjectService(ProjectRepository projectRepository, ProfileRepository profileRepository) {
        this.projectRepository = projectRepository;
        this.profileRepository = profileRepository;
    }

    public List<Project> getMyProjects(String ownerEmail) {
        return projectRepository.findByProfileId(profileId(ownerEmail));
    }

    public boolean canAddMore(String ownerEmail) {
        return projectRepository.countByProfileId(profileId(ownerEmail)) < MAX_PROJECTS;
    }

    public Project getOwned(long id, String ownerEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dự án", id));
        if (!project.ownerEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Dự án " + id + " không thuộc hồ sơ của " + ownerEmail);
        }
        return project;
    }

    public ProjectForm toForm(Project p) {
        ProjectForm form = new ProjectForm();
        form.setName(p.name());
        form.setRole(p.role());
        form.setTechStack(p.techStack());
        form.setGithubUrl(p.githubUrl());
        form.setDemoUrl(p.demoUrl());
        form.setDescription(p.description());
        return form;
    }

    @Transactional
    public void create(String ownerEmail, ProjectForm form) {
        long profileId = profileId(ownerEmail);
        if (projectRepository.countByProfileId(profileId) >= MAX_PROJECTS) {
            throw new BusinessException("Mỗi hồ sơ chỉ có tối đa " + MAX_PROJECTS + " dự án. Hãy xoá bớt dự án cũ.");
        }
        projectRepository.insert(profileId, form.getName().trim(), form.getRole().trim(), form.getTechStack().trim(),
                form.getGithubUrl().trim(), blankToNull(form.getDemoUrl()), blankToNull(form.getDescription()));
    }

    @Transactional
    public void update(long id, String ownerEmail, ProjectForm form) {
        getOwned(id, ownerEmail);
        projectRepository.update(id, form.getName().trim(), form.getRole().trim(), form.getTechStack().trim(),
                form.getGithubUrl().trim(), blankToNull(form.getDemoUrl()), blankToNull(form.getDescription()));
    }

    @Transactional
    public Project delete(long id, String ownerEmail) {
        Project project = getOwned(id, ownerEmail);
        projectRepository.delete(id);
        return project;
    }

    private long profileId(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"))
                .profileId();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
