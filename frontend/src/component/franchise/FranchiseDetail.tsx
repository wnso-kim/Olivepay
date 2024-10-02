import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { franchise, franchiseCategory } from '../../types/franchise';
import { toggleLike } from '../../api/franchiseApi';
import { BackButton, Card, Coupon, EmptyData, Button, Loader } from '../common';
import { HeartIcon as HeartSolidIcon } from '@heroicons/react/24/solid';
import { HeartIcon as HeartOutlineIcon } from '@heroicons/react/24/outline';
import { acquireCoupon } from '../../api/couponApi';
import { franchiseReviewAtom } from '../../atoms/reviewAtom';
import { useAtom } from 'jotai';
import { getFranchiseReview } from '../../api/reviewApi';

const FranchiseDetail: React.FC<{
  state: string;
  franchise: franchise;
  onClick: () => void;
}> = ({ franchise, onClick, state }) => {
  const navigate = useNavigate();
  const [reviews, setReviews] = useAtom(franchiseReviewAtom);
  const [isLiked, setIsLiked] = useState(franchise.isLiked);
  const [index, setIndex] = useState<number>(0);
  const [hasMore, setHasMore] = useState<boolean>(true);

  const { data, error, isLoading, isSuccess } = useQuery({
    queryKey: ['franchiseReview'],
    queryFn: () => getFranchiseReview(franchise.franchiseId, index),
    staleTime: 1000 * 60 * 5,
  });

  useEffect(() => {
    if (data && isSuccess) {
      setReviews(data.reviews);
      setIndex(data.nextIndex);
      setHasMore(data.reviews?.length >= 20);
    }
  }, [data, isSuccess, setReviews, setIndex, setHasMore]);

  if (isLoading) return <Loader />;

  if (error) return <div>리뷰 목록 로딩 실패</div>;

  const handleLike = () => {
    toggleLike(franchise.franchiseId);
    setIsLiked(!isLiked);
  };

  const handleDownloadCoupon = (couponUnit: number, franchiseId: number) => {
    acquireCoupon(couponUnit, franchiseId);
  };

  const handleDonateClick = () => {
    navigate('/donate', { state: { franchiseId: franchise.franchiseId } });
  };

  const getFranchiseCategoryLabel = (category: franchiseCategory | string) => {
    return (
      franchiseCategory[category as keyof typeof franchiseCategory] || '기타'
    );
  };

  const handleLoadMore = async () => {
    const result = await getFranchiseReview(franchise.franchiseId, index);
    if (result.reviews.length < 20) {
      setHasMore(false);
    }
    setIndex(result.nextIndex);
    setReviews((prev) => [...prev, ...result.reviews]);
  };

  return (
    <section>
      <div className="flex items-center justify-between">
        <BackButton onClick={onClick} />
        <h2 className="text-lg font-semibold">{franchise.franchiseName}</h2>
        <div className="w-8" />
      </div>
      <div className="my-2 mt-6 flex items-center justify-between text-base">
        <div className="flex items-center gap-4">
          <p>분류: {getFranchiseCategoryLabel(franchise.category)}</p>
        </div>
        <div className="flex items-center gap-1">
          {isLiked ? (
            <HeartSolidIcon className="size-6 text-RED" onClick={handleLike} />
          ) : (
            <HeartOutlineIcon
              className="size-6 text-RED"
              onClick={handleLike}
            />
          )}
        </div>
      </div>
      <p className="text-base">주소: {franchise.address}</p>

      {!state && (
        <div className="mt-4 flex flex-col items-center gap-4">
          <p className="text-md font-semibold">쿠폰 보유 현황</p>
          {franchise.coupon2 === 0 && franchise.coupon4 === 0 ? (
            <EmptyData label="미사용 쿠폰이 없습니다" />
          ) : (
            <>
              {franchise.coupon2 !== 0 && (
                <Coupon
                  storeName={franchise.franchiseName}
                  cost={2000}
                  count={franchise.coupon2}
                  onClick={() =>
                    handleDownloadCoupon(2000, franchise.franchiseId)
                  }
                />
              )}
              {franchise.coupon4 !== 0 && (
                <Coupon
                  storeName={franchise.franchiseName}
                  cost={4000}
                  count={franchise.coupon4}
                  onClick={() =>
                    handleDownloadCoupon(4000, franchise.franchiseId)
                  }
                />
              )}
            </>
          )}
        </div>
      )}

      <div className="mb-20 mt-4">
        <p className="mb-2 text-center text-md font-semibold">가맹점 리뷰</p>
        {reviews?.length === 0 && <EmptyData label="리뷰가 없습니다." />}
        {reviews?.map((review) => (
          <Card
            variant="review"
            key={review.reviewId}
            title={review.memberName || ''}
            content={review.content}
            score={review.stars}
          />
        ))}
        <div className="mt-2 text-center">
          {hasMore && (
            <Button
              label="더보기"
              variant="secondary"
              onClick={handleLoadMore}
            />
          )}
        </div>
      </div>
      {state === 'donate' && (
        <Button
          label="후원하기"
          variant="primary"
          className="my-10"
          onClick={handleDonateClick}
        />
      )}
    </section>
  );
};

export default FranchiseDetail;
