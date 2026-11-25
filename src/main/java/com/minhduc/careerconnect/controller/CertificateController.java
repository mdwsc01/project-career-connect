package com.minhduc.careerconnect.controller;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minhduc.careerconnect.dto.CertificateForm;
import com.minhduc.careerconnect.exception.InvalidFileException;
import com.minhduc.careerconnect.repository.CertificateRepository.Certificate;
import com.minhduc.careerconnect.service.CertificateService;

/**
 * Chứng chỉ của sinh viên và xem/tải file minh chứng (task T4E796D).
 */
@Controller
public class CertificateController {

    private static final String REDIRECT_PROFILE = "redirect:/me/profile#chung-chi";

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/me/certificates/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CertificateForm());
        model.addAttribute("maxSizeMb", certificateService.getMaxSizeMb());
        return "me/certificates/form";
    }

    @PostMapping("/me/certificates/new")
    public String create(@Valid @ModelAttribute("form") CertificateForm form, BindingResult bindingResult,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                certificateService.create(principal.getName(), form, file);
            } catch (InvalidFileException ex) {
                model.addAttribute("fileError", ex.getMessage());
            }
        }
        if (bindingResult.hasErrors() || model.containsAttribute("fileError")) {
            // Các ô chữ được giữ lại; ô file phải chọn lại (trình duyệt không cho điền sẵn file)
            model.addAttribute("maxSizeMb", certificateService.getMaxSizeMb());
            return "me/certificates/form";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm chứng chỉ \"" + form.getName().trim() + "\".");
        return REDIRECT_PROFILE;
    }

    @PostMapping("/me/certificates/{id}/delete")
    public String delete(@PathVariable long id, Principal principal, RedirectAttributes redirectAttributes) {
        Certificate removed = certificateService.delete(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã xoá chứng chỉ \"" + removed.name() + "\" và file minh chứng.");
        return REDIRECT_PROFILE;
    }

    /**
     * Xem (mặc định, mở trong trình duyệt) hoặc tải file (?download=true).
     * Chỉ chủ hồ sơ và ADMIN; người khác -> 403. Không nằm dưới /me/** để admin cũng mở được.
     */
    @GetMapping("/files/certificates/{id}")
    public ResponseEntity<Resource> file(@PathVariable long id,
                                         @RequestParam(defaultValue = "false") boolean download,
                                         Authentication authentication) {
        Certificate certificate = certificateService.getForFileAccess(id, authentication);
        Resource resource = certificateService.loadFile(certificate);
        ContentDisposition disposition = (download ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(certificate.fileOriginalName(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(certificate.fileContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(resource);
    }
}
