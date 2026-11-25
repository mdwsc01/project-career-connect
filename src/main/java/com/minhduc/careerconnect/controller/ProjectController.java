package com.minhduc.careerconnect.controller;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.ProjectForm;
import com.minhduc.careerconnect.repository.ProjectRepository.Project;
import com.minhduc.careerconnect.service.ProjectService;

/**
 * Thêm/sửa/xoá dự án của chính mình (task T4E796C).
 */
@Controller
@RequestMapping("/me/projects")
public class ProjectController {

    private static final String REDIRECT_PROFILE = "redirect:/me/profile#du-an";

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/new")
    public String newForm(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (!projectService.canAddMore(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mỗi hồ sơ chỉ có tối đa " + ProjectService.MAX_PROJECTS + " dự án. Hãy xoá bớt dự án cũ.");
            return REDIRECT_PROFILE;
        }
        model.addAttribute("form", new ProjectForm());
        return "me/projects/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") ProjectForm form, BindingResult bindingResult,
                         Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "me/projects/form";
        }
        projectService.create(principal.getName(), form); // quá 20 -> BusinessException -> báo lỗi đỏ
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm dự án \"" + form.getName().trim() + "\".");
        return REDIRECT_PROFILE;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Principal principal, Model model) {
        Project project = projectService.getOwned(id, principal.getName()); // của người khác -> 403
        model.addAttribute("form", projectService.toForm(project));
        model.addAttribute("projectId", id);
        return "me/projects/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("form") ProjectForm form,
                         BindingResult bindingResult, Principal principal, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            projectService.getOwned(id, principal.getName());
            model.addAttribute("projectId", id);
            return "me/projects/form";
        }
        projectService.update(id, principal.getName(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật dự án.");
        return REDIRECT_PROFILE;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id, Principal principal, RedirectAttributes redirectAttributes) {
        Project removed = projectService.delete(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá dự án \"" + removed.name() + "\".");
        return REDIRECT_PROFILE;
    }
}
