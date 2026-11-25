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

import com.minhduc.careerconnect.dto.SkillLevel;
import com.minhduc.careerconnect.dto.SkillSelectionForm;
import com.minhduc.careerconnect.service.MySkillService;
import com.minhduc.careerconnect.service.MySkillService.AddResult;

/**
 * Trang quản lý kỹ năng của sinh viên: /me/skills (task T4E796B).
 */
@Controller
@RequestMapping("/me/skills")
public class MySkillController {

    private final MySkillService mySkillService;

    public MySkillController(MySkillService mySkillService) {
        this.mySkillService = mySkillService;
    }

    @ModelAttribute("levels")
    public SkillLevel[] levels() {
        return SkillLevel.values();
    }

    @GetMapping
    public String page(Principal principal, Model model) {
        model.addAttribute("form", new SkillSelectionForm());
        fillPage(principal, model);
        return "me/skills";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("form") SkillSelectionForm form, BindingResult bindingResult,
                      Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            AddResult result = mySkillService.add(principal.getName(), form.getSkillName(), form.getLevel());
            if (result == AddResult.NOT_FOUND) {
                bindingResult.rejectValue("skillName", "notFound",
                        "Không có kỹ năng này trong danh mục. Hãy chọn từ danh sách gợi ý.");
            } else if (result == AddResult.DUPLICATE) {
                bindingResult.rejectValue("skillName", "duplicate", "Bạn đã có kỹ năng này trong hồ sơ.");
            }
        }
        if (bindingResult.hasErrors()) {
            fillPage(principal, model);
            return "me/skills";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm kỹ năng \"" + form.getSkillName().trim() + "\".");
        return "redirect:/me/skills";
    }

    @PostMapping("/{skillId}/level")
    public String changeLevel(@PathVariable long skillId, @RequestParam int level, Principal principal,
                              RedirectAttributes redirectAttributes) {
        mySkillService.changeLevel(principal.getName(), skillId, level);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật mức kỹ năng.");
        return "redirect:/me/skills";
    }

    @PostMapping("/{skillId}/delete")
    public String remove(@PathVariable long skillId, Principal principal, RedirectAttributes redirectAttributes) {
        mySkillService.remove(principal.getName(), skillId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã bỏ kỹ năng khỏi hồ sơ.");
        return "redirect:/me/skills";
    }

    private void fillPage(Principal principal, Model model) {
        model.addAttribute("grouped", mySkillService.getMySkillsGrouped(principal.getName()));
        model.addAttribute("availableSkills", mySkillService.getAvailableSkills(principal.getName()));
    }
}
