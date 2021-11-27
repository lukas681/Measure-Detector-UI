import { IPage } from 'app/entities/page/page.model';

export interface IMeasureBox {
  id?: number;
  ulx?: number | null;
  uly?: number | null;
  lrx?: number | null;
  lry?: number | null;
  measureCount?: number | null;
  comment?: string | null;
  page?: IPage | null;
}

export class MeasureBox implements IMeasureBox {
  constructor(
    public id?: number,
    public ulx?: number | null,
    public uly?: number | null,
    public lrx?: number | null,
    public lry?: number | null,
    public measureCount?: number | null,
    public comment?: string | null,
    public page?: IPage | null
  ) {}
}

export function getMeasureBoxIdentifier(measureBox: IMeasureBox): number | undefined {
  return measureBox.id;
}
