import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import {IEdition, getEditionIdentifier} from '../edition-detail.model';

export type EntityResponseType = HttpResponse<IEdition>;
export type EntityArrayResponseType = HttpResponse<IEdition[]>;

@Injectable({ providedIn: 'root' })
export class EditionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/editions');
  protected resourceUrlEditionsByProject = this.applicationConfigService.getEndpointFor('api/editionsByProject');
  protected resourceUrlAddEditionWithFile = this.applicationConfigService.getEndpointFor('api/edition/add');
  protected resourceUrlTriggerDetection = this.applicationConfigService.getEndpointFor('api/edition/runMeasureDetection');
  protected resourcePDFDownload = this.applicationConfigService.getEndpointFor('api/edition');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(edition: IEdition): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(edition);

    return this.http
      .post<IEdition>(this.resourceUrlAddEditionWithFile, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  triggerMeasureDetection(id: number): Observable<string> {
    console.warn("Firing Measure Detection");
    return this.http.post<string>(`${this.resourceUrlTriggerDetection}/${id}`, null);
  }

  update(edition: IEdition): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(edition);
    console.warn(edition)
    return this.http
      .put<IEdition>(`${this.resourceUrl}/${getEditionIdentifier(edition) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(edition: IEdition): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(edition);
    return this.http
      .patch<IEdition>(`${this.resourceUrl}/${getEditionIdentifier(edition) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEdition>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

 findByProjectId(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<IEdition[]>(`${this.resourceUrlEditionsByProject}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEdition[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEditionToCollectionIfMissing(editionCollection: IEdition[], ...editionsToCheck: (IEdition | null | undefined)[]): IEdition[] {
    const editions: IEdition[] = editionsToCheck.filter(isPresent);
    if (editions.length > 0) {
      const editionCollectionIdentifiers = editionCollection.map(editionItem => getEditionIdentifier(editionItem)!);
      const editionsToAdd = editions.filter(editionItem => {
        const editionIdentifier = getEditionIdentifier(editionItem);
        if (editionIdentifier == null || editionCollectionIdentifiers.includes(editionIdentifier)) {
          return false;
        }
        editionCollectionIdentifiers.push(editionIdentifier);
        return true;
      });
      return [...editionsToAdd, ...editionCollection];
    }
    return editionCollection;
  }

  downloadAnnotatedPDF(editionID: number): Observable<Blob> {
    return this.http.request("GET", `${this.resourcePDFDownload}/${editionID}/getFullPDF`,
      { responseType: 'blob',
        reportProgress: true }
    );
  }

  downloadUnannotatedPDF(editionID: number): Observable<Blob> {
    return this.http.request("GET", `${this.resourcePDFDownload}/${editionID}/getFullPDFWithoutAnnotations`,
      { responseType: 'blob',
        reportProgress: true }
    );
  }

  downloadMEI(editionID: number): Observable<Blob> {
    return this.http.request("GET", `${this.resourcePDFDownload}/${editionID}/getMEI`,
      { responseType: 'blob',
        reportProgress: true }
    );
  }
  protected convertDateFromClient(edition: IEdition): IEdition {
    return Object.assign({}, edition, {
      createdDate: edition.createdDate?.isValid() ? edition.createdDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((edition: IEdition) => {
        edition.createdDate = edition.createdDate ? dayjs(edition.createdDate) : undefined;
      });
    }
    return res;
  }

}
