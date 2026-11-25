package com.minhduc.careerconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.minhduc.careerconnect.dto.EducationForm;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.EducationRepository;
import com.minhduc.careerconnect.repository.EducationRepository.Education;
import com.minhduc.careerconnect.repository.ProfileRepository;

/**
 * Unit test học vấn, tập trung vào kiểm tra quyền sở hữu (task T4E796F).
 */
@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;
    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private EducationService service;

    private Education educationOf(String ownerEmail) {
        return new Education(5L, 1L, ownerEmail, "Trường Demo", "CNTT",
                LocalDate.of(2022, 9, 1), null, new BigDecimal("3.20"));
    }

    private EducationForm form() {
        EducationForm form = new EducationForm();
        form.setSchool(" Trường Mới ");
        form.setMajor(" Khoa học dữ liệu ");
        form.setStartDate(LocalDate.of(2023, 9, 1));
        return form;
    }

    @Test
    void update_hocVanCuaNguoiKhac_bi403VaKhongSua() {
        when(educationRepository.findById(5L)).thenReturn(Optional.of(educationOf("sv01@example.com")));

        assertThatThrownBy(() -> service.update(5L, "sv02@example.com", form()))
                .isInstanceOf(AccessDeniedException.class);

        verify(educationRepository, never()).update(anyLong(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void update_hocVanCuaMinh_luuDaCatKhoangTrang() {
        when(educationRepository.findById(5L)).thenReturn(Optional.of(educationOf("sv01@example.com")));

        service.update(5L, "SV01@example.com", form()); // email không phân biệt hoa thường

        verify(educationRepository).update(5L, "Trường Mới", "Khoa học dữ liệu",
                LocalDate.of(2023, 9, 1), null, null);
    }

    @Test
    void delete_hocVanCuaMinh_xoaVaTraVeMucDaXoa() {
        when(educationRepository.findById(5L)).thenReturn(Optional.of(educationOf("sv01@example.com")));

        Education removed = service.delete(5L, "sv01@example.com");

        assertThat(removed.school()).isEqualTo("Trường Demo");
        verify(educationRepository).delete(5L);
    }

    @Test
    void getOwned_idKhongTonTai_baoKhongTimThay() {
        when(educationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOwned(999L, "sv01@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
