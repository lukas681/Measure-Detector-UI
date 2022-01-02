import * as dayjs from 'dayjs';
import { IPage } from 'app/entities/page/page.model';
import { IProject } from 'app/entities/project/project.model';
import { EditionType } from 'app/entities/enumerations/edition-type.model';
import {ApiOrchEditionWithFile} from "../../shared/model/openapi/model/apiOrchEditionWithFile";
import {ApiOrchMeasureBox} from "../../shared/model/openapi/model/apiOrchMeasureBox";

export interface IEdition {
  id?: number;
  title?: string;
  createdDate?: dayjs.Dayjs | null;
  type?: EditionType | null;
  description?: string | null;
  pDFFileName?: string | null;
  pages?: IPage[] | null;
  project?: IProject | null;
  projectId?: number | null;
  pdfFile?: File | null;
}

export class Edition implements IEdition{
  constructor(
    public id?: number,
    public title?: string,
    public createdDate?: dayjs.Dayjs | null,
    public type?: EditionType | null,
    public description?: string | null,
    public pDFFileName?: string | null,
    public pages?: IPage[] | null,
    public project?: IProject | null,
    public projectId?: number | null,
    public pdfFile?: File | null
) {}
}

export class ApiOrchMeasureBoxImpl implements ApiOrchMeasureBox {
  constructor(
    public id?: number,
    public ulx?: number,
    public lrx?: number,
    public lry?: number,
    public uly?: number,
    public measureCount?: number,
    public comment?: string
  ) {}
}

export class EditionWithFile implements ApiOrchEditionWithFile {
  constructor(
     public  id?: number,
     public  title?: string,
     public  createdDate?: string,
     public  type?: string,
     public  description?: string,
     public  pDFFileName?: string,
     public  pdfFile?: Blob,
     public  projectId?: number,
) {}
}


export function getEditionIdentifier(edition: IEdition): number | undefined {
  return edition.id;
}
