export type ChangeEventPayload = {
  value: string;
};

export interface PrintParams {
  serial: string;
  port: string;
}

export interface PrintContent {
  title: string;
  qrContent: string;
  date: string;
  validUntil: string;
}

export interface ReceiptContent {
  dateTime: string;
  shopNo: string;
  deviceNo: string;
  receiptNo: string;
  itemPrice: string;
  total: string;
  octopusPayment: string;
  octopusNo: string;
  amountDeducted: string;
  remainingValue: string;
  lastAddValueDate: string;
}