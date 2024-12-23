import { useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAtom } from 'jotai';
import { useQueries } from '@tanstack/react-query';
import { reviewAtom, unwriteReviewAtom } from '../atoms/reviewAtom';
import { getReviews, getMissReviews, deleteReview } from '../api/reviewApi';

import {
  Layout,
  BackButton,
  PageTitle,
  Button,
  Card,
  Loader,
  EmptyData,
} from '../component/common';
import { useState, useEffect } from 'react';
import { formatDate } from '../utils/dateUtils';
import { Helmet } from 'react-helmet';

const ReviewPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [unwriteReviews, setUnwriteReviews] = useAtom(unwriteReviewAtom);
  const [reviews, setReviews] = useAtom(reviewAtom);
  const [reviewIndex, setReviewIndex] = useState<number>(0);
  const [hasMore, setHasMore] = useState(true);

  const queries = useQueries({
    queries: [
      {
        queryKey: ['review'],
        queryFn: () => getReviews(),
        staleTime: 1000 * 60 * 5,
      },
      {
        queryKey: ['availableReview'],
        queryFn: () => getMissReviews(),
      },
    ],
  });

  const [
    {
      data: reviewData,
      error: reviewError,
      isLoading: reviewLoading,
      isSuccess: reviewSuccess,
      refetch,
    },
    {
      data: missReviewData,
      error: missReviewError,
      isLoading: missReviewLoading,
      isSuccess: missReviewSuccess,
    },
  ] = queries;

  useEffect(() => {
    if (location.state?.refresh) {
      setTimeout(() => {
        refetch().then(() => {
          queries[1].refetch().then(() => {
            navigate('/review', { state: { refresh: false } });
          });
        });
      }, 500);
    }
  }, [location.state, refetch, queries, navigate]);

  useEffect(() => {
    if (missReviewSuccess && missReviewData) {
      setUnwriteReviews(missReviewData);
    }
  }, [missReviewData, missReviewSuccess, setUnwriteReviews]);

  useEffect(() => {
    if (reviewSuccess && reviewData) {
      setReviews(reviewData.contents);
      setReviewIndex(reviewData.nextIndex);
      setHasMore(reviewData.contents.length >= 20);
    }
  }, [reviewData, reviewSuccess, setReviews]);

  const handleLoadMore = useCallback(async () => {
    const result = await getReviews(reviewIndex);
    if (result.contents.length < 20) {
      setHasMore(false);
    }
    setReviewIndex(result.nextIndex);
    setReviews((prev) => [...prev, ...result.contents]);
  }, [reviewIndex, setHasMore, setReviewIndex, setReviews]);

  const handleNavigateToWriteReview = useCallback(
    (
      franchiseId: number,
      franchiseName: string,
      createdAt: string,
      paymentId: number,
    ) => {
      navigate(`/review/write/${franchiseId}`, {
        state: {
          franchiseName: franchiseName,
          createdAt: createdAt,
          paymentId: paymentId,
        },
      });
    },
    [navigate],
  );

  if (reviewLoading || missReviewLoading) return <Loader />;

  if (reviewError || missReviewError) return <div>에러</div>;

  const handleDelete = (reviewId: number) => {
    deleteReview(reviewId);
    setReviews((prevReviews) =>
      prevReviews.filter((review) => review.reviewId !== reviewId),
    );
  };

  const handleNavigateHome = () => {
    navigate('/home');
  };

  return (
    <>
      <Helmet>
        <meta
          name="description"
          content="결식 아동이 자신이 작성한 리뷰를 조회하고 삭제할 수 있으며 작성하지 않은 리뷰에 대해 작성할 수 있습니다."
        />
      </Helmet>
      <Layout className="px-8">
        <header className="mt-4 flex items-center justify-between">
          <BackButton onClick={handleNavigateHome} />
          <PageTitle title="리뷰 관리" />
          <div className="w-8" />
        </header>
        <main className="mt-4">
          <section>
            {unwriteReviews?.length > 0 && (
              <p className="border-b-2 border-DARKBASE pb-4 pl-2 text-base">
                아직 작성하지 않은 리뷰가 있어요 ❗
              </p>
            )}
            <div className="flex flex-col">
              {unwriteReviews?.map((review) => {
                return (
                  <div
                    className="flex items-center gap-4 border-b-2 border-dashed p-2 text-base"
                    key={review.franchise.id + review.createdAt}
                  >
                    <div className="flex-1">
                      <div className="text-TERTIARY">
                        {formatDate(review.createdAt)}
                      </div>
                      <div className="text-md font-semibold">
                        {review.franchise.name}
                      </div>
                    </div>
                    <Button
                      variant="text"
                      label="작성하기"
                      onClick={() =>
                        handleNavigateToWriteReview(
                          review.franchise.id,
                          review.franchise.name,
                          review.createdAt,
                          review.paymentId,
                        )
                      }
                    />
                  </div>
                );
              })}
            </div>
          </section>
          <section className="mb-24 mt-4">
            <p className="mb-2 border-b-2 border-DARKBASE p-2 font-title text-md">
              📝 내가 쓴 리뷰
            </p>
            {reviews.length === 0 && (
              <EmptyData label="작성한 리뷰가 없습니다." />
            )}
            {reviews?.map((review) => (
              <div key={review.reviewId}>
                <Card
                  variant="review"
                  title={review.franchise?.name || ''}
                  stars={review.stars}
                  content={review.content}
                  onClick={() => handleDelete(review.reviewId)}
                />
              </div>
            ))}
            <div className="mt-4 text-center">
              {hasMore && (
                <Button
                  label="더보기"
                  variant="secondary"
                  onClick={handleLoadMore}
                />
              )}
            </div>
          </section>
        </main>
      </Layout>
    </>
  );
};

export default ReviewPage;
