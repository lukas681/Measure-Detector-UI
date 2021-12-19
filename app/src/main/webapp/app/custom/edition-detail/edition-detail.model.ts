import * as dayjs from 'dayjs';
import { IPage } from 'app/entities/page/page.model';
import { IProject } from 'app/entities/project/project.model';
import { EditionType } from 'app/entities/enumerations/edition-type.model';

export interface IEdition {
  id?: number;
  title?: string;
  createdDate?: dayjs.Dayjs | null;
  type?: EditionType | null;
  description?: string | null;
  pDFFileName?: string | null;
  pages?: IPage[] | null;
  project?: IProject | null;
}

export class Edition implements IEdition {
  constructor(
    public id?: number,
    public title?: string,
    public createdDate?: dayjs.Dayjs | null,
    public type?: EditionType | null,
    public description?: string | null,
    public pDFFileName?: string | null,
    public pages?: IPage[] | null,
    public project?: IProject | null
  ) {}
}

export function getEditionIdentifier(edition: IEdition): number | undefined {
  return edition.id;
}
