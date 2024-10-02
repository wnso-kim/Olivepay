import { useNavigate, useLocation } from 'react-router-dom';
import {
  PageTitle,
  BackButton,
  Layout,
  Input,
  Button,
} from '../component/common';
import { Helmet } from 'react-helmet';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();

  const location = useLocation();
  const { loginType } = location.state || {};

  return (
    <>
      <Helmet>
        <meta
          name="description"
          content="결식 아동과 가맹점주가 로그인을 할 수 있습니다."
        />
      </Helmet>
      <Layout hasBottomTab={false}>
        <header className="flex w-full items-center justify-between px-10 pb-10 pt-4">
          <BackButton />
          <div className="flex-grow text-center">
            <PageTitle title="로그인" />
          </div>
          <div className="w-8" />
        </header>
        <main className="p-5">
          <figure className="flex flex-col gap-y-5">
            <Input
              type="id"
              className="col-span-9 border border-gray-300 px-4"
              placeholder="휴대폰번호"
            />
            <Input
              type="password"
              className="col-span-9 border border-gray-300 px-4"
              placeholder="비밀번호"
            />
          </figure>
          <p className="pt-10 text-center text-sm text-gray-400">
            아직 계정이 없으신가요?
            <span
              className="cursor-pointer text-blue-800 underline"
              onClick={() =>
                navigate('/signup', { state: { type: loginType } })
              }
            >
              회원가입
            </span>
          </p>
          <div className="py-10">
            <Button variant="primary" label="로그인하기" />
          </div>
        </main>
      </Layout>
    </>
  );
};

export default LoginPage;
