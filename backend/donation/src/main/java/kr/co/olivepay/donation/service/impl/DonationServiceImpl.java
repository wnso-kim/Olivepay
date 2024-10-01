package kr.co.olivepay.donation.service.impl;

import jakarta.transaction.Transactional;
import kr.co.olivepay.core.donation.dto.req.CouponListReq;
import kr.co.olivepay.core.donation.dto.res.CouponRes;
import kr.co.olivepay.core.franchise.dto.res.FranchiseMyDonationRes;
import kr.co.olivepay.donation.dto.req.DonationMyReq;
import kr.co.olivepay.donation.dto.req.DonationReq;
import kr.co.olivepay.donation.dto.res.DonationMyRes;
import kr.co.olivepay.donation.dto.res.DonationTotalRes;
import kr.co.olivepay.donation.entity.Donation;
import kr.co.olivepay.donation.entity.Donor;
import kr.co.olivepay.donation.enums.CouponUnit;
import kr.co.olivepay.donation.global.enums.NoneResponse;
import kr.co.olivepay.donation.global.enums.SuccessCode;
import kr.co.olivepay.core.global.dto.res.PageResponse;
import kr.co.olivepay.donation.global.response.SuccessResponse;
import kr.co.olivepay.donation.mapper.CouponMapper;
import kr.co.olivepay.donation.mapper.DonationMapper;
import kr.co.olivepay.donation.mapper.DonorMapper;
import kr.co.olivepay.donation.repository.CouponRepository;
import kr.co.olivepay.donation.repository.CouponUserRepository;
import kr.co.olivepay.donation.repository.DonationRepository;
import kr.co.olivepay.donation.repository.DonorRepository;
import kr.co.olivepay.donation.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static kr.co.olivepay.donation.global.enums.SuccessCode.DONATION_SUCCESS;
import static kr.co.olivepay.donation.global.enums.SuccessCode.DONATION_TOTAL_SUCCESS;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final DonorRepository donorRepository;
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final DonorMapper donorMapper;
    private final CouponMapper couponMapper;
    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;

    @Override
    @Transactional
    public SuccessResponse<NoneResponse> donate(DonationReq request) {
        // 전화번호로 식별 후 이메일 갱신 또는 삽입하여 donor 객체 생성
        Donor donor = updateOrCreateDonor(request);
        // 후원 객체 생성
        Donation donation = donationRepository.save(donationMapper.toEntity(donor, request));
        // 쿠폰 생성
        couponRepository.save(couponMapper.toEntity(donation, CouponUnit.TWO, request));
        couponRepository.save(couponMapper.toEntity(donation, CouponUnit.FOUR, request));

        // TODO : 기부금액을 핀테크 api를 사용하여 계좌이체 처리
        return new SuccessResponse<>(DONATION_SUCCESS, NoneResponse.NONE);
    }

    @Override
    public SuccessResponse<DonationTotalRes> getDonationTotal() {
        Long total = donationRepository.sumMoney();
        Long mealCount = couponUserRepository.countByIsUsed(true);
        DonationTotalRes response = DonationTotalRes.builder()
                                                    .total(total)
                                                    .mealCount(mealCount)
                                                    .build();
        return new SuccessResponse<>(DONATION_TOTAL_SUCCESS, response);
    }

    @Override
    public SuccessResponse<PageResponse<List<DonationMyRes>>> getMyDonation(DonationMyReq request, Long index) {
        List<DonationMyRes> response = new ArrayList<>();
        Long nextIndex = index;
        Optional<Donor> donor = donorRepository.findByEmailAndPhoneNumber(request.email(), request.phoneNumber());
        // 후원자가 있는 경우에만 후원 내역 조회
        if (donor.isPresent()) {
            List<Donation> donations = donationRepository.getMyDonation(donor.get(), index);
            nextIndex = donations.isEmpty() ? nextIndex : donations.get(donations.size() - 1)
                                                                   .getId();
            // TODO: 가맹점 상세 조회 feign client로 요청해서 받아오기
            List<FranchiseMyDonationRes> franchiseResponse = new ArrayList<>();

            // Donation과 franchiseResponse를 통해 DonationMyRes로 매핑
            response = donations.stream()
                                .map(donation -> mapToDonationMyRes(donation, franchiseResponse))
                                .toList();
        }
        return new SuccessResponse<>(SuccessCode.DONATION_MY_SUCCESS, new PageResponse<>(nextIndex, response));
    }

    @Override
    public SuccessResponse<CouponRes> getFranchiseCoupon(Long franchiseId) {
        List<CouponRes> couponRes = couponRepository.getCouponCountsByFranchiseId(List.of(franchiseId));
        return new SuccessResponse<>(SuccessCode.COUPON_GET_SUCCESS, couponRes.get(0));
    }

    @Override
    public SuccessResponse<List<CouponRes>> getFranchiseListCoupon(CouponListReq request) {
        List<CouponRes> couponRes = couponRepository.getCouponCountsByFranchiseId(request.franchiseIdList());
        return new SuccessResponse<>(SuccessCode.COUPON_LIST_GET_SUCCESS, couponRes);
    }

    private Donor updateOrCreateDonor(DonationReq request) {
        Donor donor = donorRepository.findByPhoneNumber(request.phoneNumber())
                                     // 존재하는 경우 이메일 업데이트
                                     .map(existingDonor -> {
                                         existingDonor.updateEmail(request.email());
                                         return existingDonor;
                                     })
                                     // 존재하지 않는 경우 새로운 Donor 생성
                                     .orElseGet(() -> donorMapper.toEntity(request));
        return donorRepository.save(donor);
    }

    private DonationMyRes mapToDonationMyRes(Donation donation, List<FranchiseMyDonationRes> franchiseResponse) {
        Optional<FranchiseMyDonationRes> franchiseOpt =
                franchiseResponse.stream()
                                 .filter(fr -> fr.franchiseId()
                                                 .equals(donation.getFranchiseId()))
                                 .findFirst();

        // TODO : feign client 연결 이후 더미 값 제거
        return franchiseOpt.map(franchiseRes -> donationMapper.toDonationMyRes(donation, franchiseRes))
                           .orElse(DonationMyRes.builder()
                                                .franchiseId(donation.getFranchiseId())
                                                .name("가맹점1")  // 기본 값
                                                .address("서울시 강남구")  // 기본 값
                                                .money(donation.getMoney()
                                                               .intValue())
                                                .date(new Date())  // 기본 값
                                                .build());
    }
}
