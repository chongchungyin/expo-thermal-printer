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