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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.CreatePartnerForm;
import com.minhduc.careerconnect.dto.UserSearchFilter;
import com.minhduc.careerconnect.repository.UserRepository.UserRow;
import com.minhduc.careerconnect.service.AdminUserService;

/**
 * Trang quản trị tài khoản, chỉ ADMIN (đã chặn ở SecurityConfig: /admin/**).
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public String list(@ModelAttribute("filter") UserSearchFilter filter, Model model, Principal principal) {
        model.addAttribute("result", adminUserService.search(filter));
        model.addAttribute("currentEmail", principal.getName());
        return "admin/users/list";
    }

    @GetMapping("/new-partner")
    public String newPartnerForm(Model model) {
        model.addAttribute("form", new CreatePartnerForm());
        return "admin/users/new-partner";
    }

    @PostMapping("/new-partner")
    public String createPartner(@Valid @ModelAttribute("form") CreatePartnerForm form, BindingResult bindingResult,
                                Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/users/new-partner";
        }
        adminUserService.createPartner(form, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã tạo tài khoản đối tác " + form.getEmail().trim().toLowerCase() + ".");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/lock")
    public String lock(@PathVariable long id, @RequestParam(required = false) String reason,
                       UserSearchFilter filter, Principal principal, RedirectAttributes redirectAttributes) {
        UserRow user = adminUserService.lock(id, reason, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã khoá tài khoản " + user.email() + ".");
        return redirectToList(filter, redirectAttributes);
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable long id, UserSearchFilter filter, Principal principal,
                         RedirectAttributes redirectAttributes) {
        UserRow user = adminUserService.unlock(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã mở khoá tài khoản " + user.email() + ".");
        return redirectToList(filter, redirectAttributes);
    }

    /**
     * Quay lại danh sách, giữ nguyên bộ lọc và trang đang xem.
     */
    private String redirectToList(UserSearchFilter filter, RedirectAttributes redirectAttributes) {
        if (filter.getQ() != null && !filter.getQ().isBlank()) redirectAttributes.addAttribute("q", filter.getQ());
        if (filter.getRole() != null && !filter.getRole().isBlank())
            redirectAttributes.addAttribute("role", filter.getRole());
        if (filter.getStatus() != null && !filter.getStatus().isBlank())
            redirectAttributes.addAttribute("status", filter.getStatus());
        redirectAttributes.addAttribute("page", filter.getPage());
        return "redirect:/admin/users";
    }
}
