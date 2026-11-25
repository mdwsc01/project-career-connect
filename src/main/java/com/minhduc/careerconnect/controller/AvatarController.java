package com.minhduc.careerconnect.controller;

import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.exception.InvalidFileException;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;
import com.minhduc.careerconnect.service.AvatarService;
import com.minhduc.careerconnect.service.MyProfileService;

/**
 * Đổi ảnh đại diện (task T4E796E).
 */
@Controller
public class AvatarController {

    private final AvatarService avatarService;
    private final MyProfileService myProfileService;

    public AvatarController(AvatarService avatarService, MyProfileService myProfileService) {
        this.avatarService = avatarService;
        this.myProfileService = myProfileService;
    }

    @GetMapping("/me/avatar")
    public String page(Principal principal, Model model) {
        fillPage(principal, model);
        return "me/avatar";
    }

    @PostMapping("/me/avatar")
    public String upload(@RequestParam(value = "file", required = false) MultipartFile file, Principal principal,
                         Model model, RedirectAttributes redirectAttributes) {
        try {
            avatarService.upload(principal.getName(), file);
        } catch (InvalidFileException ex) {
            model.addAttribute("fileError", ex.getMessage());
            fillPage(principal, model);
            return "me/avatar";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ảnh đại diện.");
        return "redirect:/me/profile";
    }

    @PostMapping("/me/avatar/delete")
    public String remove(Principal principal, RedirectAttributes redirectAttributes) {
        avatarService.remove(principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Đã bỏ ảnh đại diện.");
        return "redirect:/me/profile";
    }

    /**
     * Phục vụ ảnh đại diện cho người đã đăng nhập (sau này đối tác xem hồ sơ cũng cần ảnh).
     */
    @GetMapping("/files/avatars/{profileId}")
    public ResponseEntity<Resource> avatar(@PathVariable long profileId) {
        Resource resource = avatarService.load(profileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatarService.contentTypeOf(profileId)))
                .cacheControl(CacheControl.noCache().cachePrivate())
                .body(resource);
    }

    private void fillPage(Principal principal, Model model) {
        MyProfile profile = myProfileService.getMyProfile(principal.getName());
        model.addAttribute("profile", profile);
        model.addAttribute("avatarInitial", AvatarService.initialOf(profile.fullName()));
        model.addAttribute("avatarColor", AvatarService.colorOf(profile.fullName()));
        model.addAttribute("maxSizeMb", AvatarService.MAX_BYTES / 1024 / 1024);
    }
}
