package com.minhduc.careerconnect.controller;

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

import com.minhduc.careerconnect.dto.SkillCategory;
import com.minhduc.careerconnect.dto.SkillForm;
import com.minhduc.careerconnect.repository.SkillRepository.Skill;
import com.minhduc.careerconnect.service.SkillService;
import com.minhduc.careerconnect.service.SkillService.SaveResult;

/**
 * Trang /admin/skills, chỉ ADMIN (SecurityConfig chặn /admin/**).
 */
@Controller
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final SkillService skillService;

    public AdminSkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Danh sách nhóm cho ô chọn, có sẵn ở mọi trang của controller này.
     */
    @ModelAttribute("categories")
    public SkillCategory[] categories() {
        return SkillCategory.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) SkillCategory category,
                       @RequestParam(required = false) String visibility,
                       Model model) {
        model.addAttribute("skills", skillService.search(q, category, visibility));
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("visibility", visibility);
        return "admin/skills/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new SkillForm());
        return "admin/skills/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") SkillForm form, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors() && skillService.create(form) == SaveResult.DUPLICATE_NAME) {
            bindingResult.rejectValue("name", "duplicate", "Kỹ năng này đã có trong danh mục (không phân biệt hoa thường)");
        }
        if (bindingResult.hasErrors()) {
            return "admin/skills/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm kỹ năng \"" + form.getName().trim() + "\".");
        return "redirect:/admin/skills";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("form", skillService.toForm(skillService.get(id)));
        model.addAttribute("skillId", id);
        return "admin/skills/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("form") SkillForm form,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors() && skillService.update(id, form) == SaveResult.DUPLICATE_NAME) {
            bindingResult.rejectValue("name", "duplicate", "Kỹ năng này đã có trong danh mục (không phân biệt hoa thường)");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("skillId", id);
            return "admin/skills/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật kỹ năng \"" + form.getName().trim() + "\".");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/hide")
    public String hide(@PathVariable long id, RedirectAttributes redirectAttributes) {
        Skill skill = skillService.setHidden(id, true);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã ẩn kỹ năng \"" + skill.name() + "\". Sinh viên đang dùng vẫn giữ, nhưng không thể chọn mới.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/show")
    public String show(@PathVariable long id, RedirectAttributes redirectAttributes) {
        Skill skill = skillService.setHidden(id, false);
        redirectAttributes.addFlashAttribute("successMessage", "Đã hiện lại kỹ năng \"" + skill.name() + "\".");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        Skill skill = skillService.delete(id); // đang được dùng -> BusinessException -> báo lỗi đỏ
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá kỹ năng \"" + skill.name() + "\".");
        return "redirect:/admin/skills";
    }
}
