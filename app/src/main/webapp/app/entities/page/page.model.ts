import { IMeasureBox } from 'app/entities/measure-box/measure-box.model';
import { IEdition } from 'app/entities/edition/edition.model';

export interface IPage {
  id?: number;
  pageNr?: number;
  imgFileReference?: string | null;
  measureNumberOffset?: number | null;
  nextPage?: number | null;
  measureBoxes?: IMeasureBox[] | null;
  edition?: IEdition | null;
}

export class Page implements IPage {
  constructor(
    public id?: number,
    public pageNr?: number,
    public imgFileReference?: string | null,
    public measureNumberOffset?: number | null,
    public nextPage?: number | null,
    public measureBoxes?: IMeasureBox[] | null,
    public edition?: IEdition | null
  ) {}
}

export function getPageIdentifier(page: IPage): number | undefined {
  return page.id;
}
