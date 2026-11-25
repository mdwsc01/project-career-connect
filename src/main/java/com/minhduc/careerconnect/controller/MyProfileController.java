package com.minhduc.careerconnect.controller;

import java.security.Principal;
import java.time.LocalDate;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.MyProfileForm;
import com.minhduc.careerconnect.dto.SkillLevel;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;
import com.minhduc.careerconnect.service.AvatarService;
import com.minhduc.careerconnect.service.CertificateService;
import com.minhduc.careerconnect.service.EducationService;
import com.minhduc.careerconnect.service.MyProfileService;
import com.minhduc.careerconnect.service.MySkillService;
import com.minhduc.careerconnect.service.ProjectService;

/** Trang "Hồ sơ của tôi" - chỉ STUDENT (SecurityConfig chặn /me/**), chỉ hồ sơ của chính mình. */
@Controller
@RequestMapping("/me/profile")
public class MyProfileController {

    private static final int MIN_AGE = 15;
    private static final LocalDate MIN_DATE = LocalDate.of(1950, 1, 1);

    private final MyProfileService myProfileService;
    private final EducationService educationService;
    private final MySkillService mySkillService;
    private final ProjectService projectService;
    private final CertificateService certificateService;

    public MyProfileController(MyProfileService myProfileService, EducationService educationService,
                               MySkillService mySkillService, ProjectService projectService,
                               CertificateService certificateService) {
        this.myProfileService = myProfileService;
        this.educationService = educationService;
        this.mySkillService = mySkillService;
        this.projectService = projectService;
        this.certificateService = certificateService;
    }

    @GetMapping
    public String view(Principal principal, Model model) {
        MyProfile profile = myProfileService.getMyProfile(principal.getName());
        model.addAttribute("profile", profile);
        model.addAttribute("avatarInitial", AvatarService.initialOf(profile.fullName()));
        model.addAttribute("avatarColor", AvatarService.colorOf(profile.fullName()));
        model.addAttribute("educations", educationService.getMyEducations(principal.getName()));
        model.addAttribute("skillGroups", mySkillService.getMySkillsGrouped(principal.getName()));
        model.addAttribute("levels", SkillLevel.values());
        model.addAttribute("projects", projectService.getMyProjects(principal.getName()));
        model.addAttribute("maxProjects", ProjectService.MAX_PROJECTS);
        model.addAttribute("certificates", certificateService.getMyCertificates(principal.getName()));
        return "me/profile";
    }

    @GetMapping("/edit")
    public String editForm(Principal principal, Model model) {
        model.addAttribute("form", myProfileService.toForm(myProfileService.getMyProfile(principal.getName())));
        return "me/profile-edit";
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("form") MyProfileForm form, BindingResult bindingResult,
                         Principal principal, RedirectAttributes redirectAttributes) {
        validateBirthDateRange(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "me/profile-edit"; // lỗi hiện dưới từng ô, dữ liệu đã nhập được giữ lại
        }
        myProfileService.update(principal.getName(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu hồ sơ.");
        return "redirect:/me/profile";
    }

    /** Ngoài @Past: không trước 1950 và đủ 15 tuổi. */
    private void validateBirthDateRange(MyProfileForm form, BindingResult bindingResult) {
        LocalDate dob = form.getDateOfBirth();
        if (dob == null || bindingResult.hasFieldErrors("dateOfBirth")) {
            return;
        }
        if (dob.isBefore(MIN_DATE)) {
            bindingResult.rejectValue("dateOfBirth", "range", "Ngày sinh không hợp lệ (phải từ năm 1950 trở đi)");
        } else if (dob.isAfter(LocalDate.now().minusYears(MIN_AGE))) {
            bindingResult.rejectValue("dateOfBirth", "range", "Bạn phải từ " + MIN_AGE + " tuổi trở lên");
        }
    }
}
