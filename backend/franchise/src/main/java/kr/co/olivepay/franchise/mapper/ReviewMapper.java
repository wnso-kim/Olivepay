package kr.co.olivepay.franchise.mapper;

import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kr.co.olivepay.core.member.dto.req.UserNicknamesReq;
import kr.co.olivepay.core.payment.dto.res.PaymentMinimalRes;
import kr.co.olivepay.franchise.dto.req.ReviewCreateReq;
import kr.co.olivepay.franchise.dto.res.EmptyReviewRes;
import kr.co.olivepay.franchise.dto.res.FranchiseMinimalRes;
import kr.co.olivepay.franchise.dto.res.FranchiseReviewRes;
import kr.co.olivepay.franchise.dto.res.UserReviewRes;
import kr.co.olivepay.franchise.entity.Franchise;
import kr.co.olivepay.franchise.entity.Review;
import kr.co.olivepay.franchise.repository.FranchiseRepository;

@Mapper(componentModel = "spring", uses = {FranchiseRepository.class})
public interface ReviewMapper {

	@Mapping(source = "memberId", target = "memberId")
	@Mapping(source = "reviewReq.content", target = "content")
	@Mapping(source = "reviewReq.stars", target = "stars")
	@Mapping(source = "reviewReq.paymentId", target = "paymentId")
	@Mapping(target = "franchise", source = "franchise")
	Review toEntity(Long memberId, ReviewCreateReq reviewReq, Franchise franchise);

	@Mapping(source = "review.id", target = "reviewId")
	@Mapping(source = "nickname", target = "memberName")
	UserReviewRes toUserReviewRes(Review review, String nickname);

	@Mapping(source = "id", target = "reviewId")
	FranchiseReviewRes toFranchiseReviewRes(Review review);

	List<FranchiseReviewRes> toFranchiseReviewResList(List<Review> reviewList);

	EmptyReviewRes toEmptyReviewRes(Integer reviewId, FranchiseMinimalRes franchise, PaymentMinimalRes payment);

	default UserNicknamesReq toUserNicknamesReq(List<Long> memberIds) {
		return UserNicknamesReq.builder()
							   .memberIds(memberIds)
							   .build();
	}
}
