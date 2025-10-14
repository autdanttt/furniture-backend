package org.frogcy.furnitureadmin.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.frogcy.furnitureadmin.shippingfee.ProvinceRepository;
import org.frogcy.furniturecommon.entity.address.Province;
import org.frogcy.furniturecommon.entity.address.Ward;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ProvinceSeeder implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() > 0) {
            System.out.println("✅ Provinces already exist. Skipping seeding...");
            return;
        }

        System.out.println("🌱 Seeding provinces and wards from API...");

        String url = "https://provinces.open-api.vn/api/v2/?depth=2";
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        List<ProvinceDTO> provinceDTOs = mapper.readValue(json, new TypeReference<>() {});

        for (ProvinceDTO pDto : provinceDTOs) {
            Province province = Province.builder()
                    .code(pDto.getCode())
                    .name(pDto.getName())
                    .codename(pDto.getCodename())
                    .divisionType(pDto.getDivision_type())
                    .phoneCode(pDto.getPhone_code())
                    .build();

            List<Ward> wards = pDto.getWards().stream()
                    .map(wDto -> Ward.builder()
                            .code(wDto.getCode())
                            .name(wDto.getName())
                            .codename(wDto.getCodename())
                            .divisionType(wDto.getDivision_type())
                            .shortCodename(wDto.getShort_codename())
                            .province(province)
                            .build()
                    )
                    .toList();

            province.setWards(wards);
            provinceRepository.save(province);
        }

        System.out.println("✅ Seeded provinces successfully!");
    }

    // Inner DTOs for JSON mapping
    @lombok.Data
    private static class ProvinceDTO {
        private Integer code;
        private String name;
        private String codename;
        private String division_type;
        private Integer phone_code;
        private List<WardDTO> wards;
    }

    @lombok.Data
    private static class WardDTO {
        private Integer code;
        private String name;
        private String codename;
        private String division_type;
        private String short_codename;
    }
}