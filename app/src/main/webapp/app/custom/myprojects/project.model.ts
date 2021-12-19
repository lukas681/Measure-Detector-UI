import * as dayjs from 'dayjs';
import { IEdition } from 'app/entities/edition/edition.model';

export interface IProject {
  id?: number;
  name?: string;
  composer?: string | null;
  createdDate?: dayjs.Dayjs | null;
  editions?: IEdition[] | null;
}

export class Project implements IProject {
  constructor(
    public id?: number,
    public name?: string,
    public composer?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public editions?: IEdition[] | null
  ) {}
}

export function getProjectIdentifier(project: IProject): number | undefined {
  return project.id;
}
