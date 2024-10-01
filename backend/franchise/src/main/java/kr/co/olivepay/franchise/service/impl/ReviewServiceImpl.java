package kr.co.olivepay.franchise.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.olivepay.core.global.dto.res.PageResponse;
import kr.co.olivepay.franchise.dto.req.ReviewCreateReq;
import kr.co.olivepay.franchise.dto.res.EmptyReviewRes;
import kr.co.olivepay.franchise.dto.res.FranchiseReviewRes;
import kr.co.olivepay.franchise.dto.res.UserReviewRes;
import kr.co.olivepay.franchise.entity.Franchise;
import kr.co.olivepay.franchise.entity.Review;
import kr.co.olivepay.franchise.global.enums.NoneResponse;
import kr.co.olivepay.franchise.global.enums.SuccessCode;
import kr.co.olivepay.franchise.global.response.SuccessResponse;
import kr.co.olivepay.franchise.mapper.ReviewMapper;
import kr.co.olivepay.franchise.repository.ReviewRepository;
import kr.co.olivepay.franchise.repository.FranchiseRepository;
import kr.co.olivepay.franchise.service.ReviewService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final FranchiseRepository franchiseRepository;
	private final ReviewMapper reviewMapper;

	/**
	 * 리뷰 등록
	 * @param memberId
	 * @param request
	 * @return
	 */
	@Override
	public SuccessResponse<NoneResponse> registerReview(Long memberId, ReviewCreateReq request) {
		Franchise franchise = franchiseRepository.getById(request.franchiseId());
		Review review = reviewMapper.toEntity(memberId, request, franchiseRepository);
		reviewRepository.save(review);
		return new SuccessResponse<>(SuccessCode.REVIEW_REGISTER_SUCCESS, NoneResponse.NONE);
	}

	/**
	 * 리뷰 삭제
	 * @param reviewId
	 * @return
	 */
	@Override
	public SuccessResponse<NoneResponse> removeReview(Long reviewId) {
		reviewRepository.deleteById(reviewId);
		return new SuccessResponse<>(SuccessCode.REVIEW_DELETE_SUCCESS, NoneResponse.NONE);
	}

	/**
	 * 내가 작성한 리뷰 조회
	 * @param memberId
	 * @return
	 */
	@Override
	public SuccessResponse<PageResponse<List<FranchiseReviewRes>>> getMyReviewList(Long memberId, Long index) {
		List<Review> reviewList = reviewRepository.findAllByMemberIdAfterIndex(memberId, index);
		List<FranchiseReviewRes> reviewResList = reviewMapper.toFranchiseReviewResList(reviewList);
		long nextIndex = reviewList.get(reviewList.size() - 1)
								   .getId();

		PageResponse<List<FranchiseReviewRes>> response = new PageResponse<>(nextIndex, reviewResList);

		return new SuccessResponse<>(
			SuccessCode.USER_REVIEW_SEARCH_SUCCESS,
			response
		);
	}

	/**
	 * 특정 가맹점의 리뷰 조회
	 * @param franchiseId
	 * @return
	 */
	@Override
	public SuccessResponse<PageResponse<List<UserReviewRes>>> getFranchiseReviewList(Long franchiseId, Long index) {
		List<Review> reviewList = reviewRepository.findAllByFranchiseIdAfterIndex(franchiseId, index);

		List<Long> memberIdList = reviewList.stream().map(Review::getMemberId).toList();
		//TODO: 멤버 서비스 호출
		//map으로 Long, String을 만든다.
		//review 순회하면서 채워넣는다.

		List<UserReviewRes> reviewResList = reviewMapper.toUserReviewResList(reviewList);
		long nextIndex = reviewList.get(reviewList.size() - 1).getId();

		PageResponse<List<UserReviewRes>> response = new PageResponse<>(nextIndex, reviewResList);

		return new SuccessResponse<>(
			SuccessCode.FRANCHISE_REVIEW_SEARCH_SUCCESS,
			response
		);
	}

	@Override
	public SuccessResponse<List<EmptyReviewRes>> getAvailableReviewList(Long memberId) {
		return null;
	}

	@Override
	public Float getAvgStars(Long franchiseId) {
		return reviewRepository.getAverageStarsByFranchiseId(franchiseId);
	}
}
