package kr.co.olivepay.franchise.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.olivepay.franchise.entity.QReview;
import kr.co.olivepay.franchise.entity.Review;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Review> findAllByMemberIdAfterIndex(Long memberId, Long index) {
		QReview review = QReview.review;
		return queryFactory.selectFrom(review)
						   .where(review.memberId.eq(memberId)
												 .and(review.id.gt(index)))
						   .orderBy(review.createdAt.asc())
						   .limit(20)
						   .fetch();
	}
}
