declare const BUTTON_VARIANTS: {
  primary: string;
  secondary: string;
  text: string;
};

declare const CARD_VARIANTS: {
  restaurant: object;
  payment: object;
  review: object;
  donation: object;
  info: object;
};

type LayoutProps = {
  children: React.ReactNode;
  className?: string;
  hasBottomTab?: boolean;
  isWhite?: boolean;
};

type BottomUpProps = {
  children: React.ReactNode;
  className?: string;
  isVisible?: boolean;
  setIsVisible?: (visible: boolean) => void;
  fromMap?: boolean;
};

type EmptyDataProps = {
  label?: string;
};

type ButtonProps = {
  className?: string;
  variant?: keyof typeof BUTTON_VARIANTS;
  label: string;
  disabled?: boolean;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
};

type BackButtonProps = {
  className?: string;
};

type InputProps = {
  name?: string;
  className?: string;
  type?: string;
  placeholder?: string;
  value?: string;
  checked?: boolean;
  maxLength?: number;
  minLength?: number;
  autoComplete?: string;
  readOnly?: boolean;
  disabled?: boolean;
  onClick?: () => void;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  required?: boolean;
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void;
  container?: string;
};

interface CardVariantStyles {
  container: string;
  header?: string;
  title: string;
  category?: string;
  score?: string;
  like?: string;
  spend?: string;
  price?: string;
  details?: string;
  content?: string;
  location?: string;
  date?: string;
}

type CardProps = {
  variant: keyof typeof CARD_VARIANTS;
  title: string;
  category?: string;
  score?: number;
  like?: number | string;
  spend?: number;
  price?: number;
  content?: string;
  details?: Array;
  location?: string;
  date?: string;
  onClick?: () => void;
  stars?: number;
};

interface StarRatingProps {
  value: number;
  canEdit?: boolean;
  onChange?: (number) => void;
  isLarge?: boolean;
}

type StepProps = {
  currentStep: number;
  steps: number;
};

type CreditCardProps = {
  cardName: string;
  cardNumber?: string;
  cardOwner?: string;
  isDefault?: boolean;
  onClick?: () => void;
};

type CouponProps = {
  couponID?: number;
  storeName?: string;
  cost?: number;
  onClick?: () => void;
  forFranchiser?: boolean;
  count?: number;
};

interface PageTitleProps {
  title: string;
}

interface KeyPadProps {
  variant: 'password' | 'money';
  onKeyPress?: (value: string | number) => void;
}

interface NavigateBoxProps {
  icon: React.ReactNode;
  text?: string;
  path: string;
  className?: string;
  bigger?: boolean;
  onClick?: () => void;
}

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  forPay?: boolean;
}
