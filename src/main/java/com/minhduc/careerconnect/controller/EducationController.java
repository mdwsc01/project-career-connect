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

import com.minhduc.careerconnect.dto.EducationForm;
import com.minhduc.careerconnect.repository.EducationRepository.Education;
import com.minhduc.careerconnect.service.EducationService;

/** Thêm/sửa/xoá học vấn của chính mình (task T4E7969). */
@Controller
@RequestMapping("/me/educations")
public class EducationController {

    private static final String REDIRECT_PROFILE = "redirect:/me/profile#hoc-van";

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new EducationForm());
        return "me/educations/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") EducationForm form, BindingResult bindingResult,
                         Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "me/educations/form";
        }
        educationService.create(principal.getName(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm học vấn.");
        return REDIRECT_PROFILE;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Principal principal, Model model) {
        Education education = educationService.getOwned(id, principal.getName()); // của người khác -> 403
        model.addAttribute("form", educationService.toForm(education));
        model.addAttribute("educationId", id);
        return "me/educations/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("form") EducationForm form,
                         BindingResult bindingResult, Principal principal, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            educationService.getOwned(id, principal.getName()); // vẫn chặn 403 kể cả khi form lỗi
            model.addAttribute("educationId", id);
            return "me/educations/form";
        }
        educationService.update(id, principal.getName(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật học vấn.");
        return REDIRECT_PROFILE;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id, Principal principal, RedirectAttributes redirectAttributes) {
        Education removed = educationService.delete(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá học vấn tại " + removed.school() + ".");
        return REDIRECT_PROFILE;
    }
}
