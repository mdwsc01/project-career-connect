package com.minhduc.careerconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.minhduc.careerconnect.dto.SkillCategory;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;
import com.minhduc.careerconnect.repository.ProfileSkillRepository;
import com.minhduc.careerconnect.repository.SkillRepository;
import com.minhduc.careerconnect.repository.SkillRepository.Skill;
import com.minhduc.careerconnect.service.MySkillService.AddResult;

/**
 * Unit test kỹ năng của sinh viên (task T4E796F).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MySkillServiceTest {

    private static final String EMAIL = "sv01@example.com";
    private static final long PROFILE_ID = 1L;

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileSkillRepository profileSkillRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private MySkillService service;

    @BeforeEach
    void setUp() {
        MyProfile profile = new MyProfile(5L, PROFILE_ID, EMAIL, "Nguyễn Văn An", null, null, null,
                "Backend Developer", null, "APPROVED", true, "nguyen-van-an-01", null);
        when(profileRepository.findByOwnerEmail(EMAIL)).thenReturn(Optional.of(profile));
    }

    @Test
    void add_kyNangHopLe_themVaoHoSo() {
        when(skillRepository.findVisibleByName("Python"))
                .thenReturn(Optional.of(new Skill(3L, "Python", SkillCategory.LANGUAGE, false, 4)));
        when(profileSkillRepository.exists(PROFILE_ID, 3L)).thenReturn(false);

        AddResult result = service.add(EMAIL, "  Python ", 2);

        assertThat(result).isEqualTo(AddResult.OK);
        verify(profileSkillRepository).insert(PROFILE_ID, 3L, 2);
    }

    @Test
    void add_kyNangDaCo_baoTrungVaKhongThem() {
        when(skillRepository.findVisibleByName("Java"))
                .thenReturn(Optional.of(new Skill(1L, "Java", SkillCategory.LANGUAGE, false, 8)));
        when(profileSkillRepository.exists(PROFILE_ID, 1L)).thenReturn(true);

        AddResult result = service.add(EMAIL, "Java", 4);

        assertThat(result).isEqualTo(AddResult.DUPLICATE);
        verify(profileSkillRepository, never()).insert(anyLong(), anyLong(), anyInt());
    }

    @Test
    void add_kyNangKhongCoHoacDaAn_baoKhongTimThay() {
        // findVisibleByName chỉ trả về kỹ năng đang hiện, nên kỹ năng bị ẩn cũng rơi vào trường hợp này
        when(skillRepository.findVisibleByName("abcxyz")).thenReturn(Optional.empty());

        AddResult result = service.add(EMAIL, "abcxyz", 3);

        assertThat(result).isEqualTo(AddResult.NOT_FOUND);
        verify(profileSkillRepository, never()).insert(anyLong(), anyLong(), anyInt());
    }

    @Test
    void changeLevel_mucNgoaiKhoang1Den5_nemLoi() {
        assertThatThrownBy(() -> service.changeLevel(EMAIL, 1L, 6))
                .isInstanceOf(BusinessException.class);
        verify(profileSkillRepository, never()).updateLevel(anyLong(), anyLong(), anyInt());
    }
}
