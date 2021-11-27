import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMeasureBox, getMeasureBoxIdentifier } from '../measure-box.model';

export type EntityResponseType = HttpResponse<IMeasureBox>;
export type EntityArrayResponseType = HttpResponse<IMeasureBox[]>;

@Injectable({ providedIn: 'root' })
export class MeasureBoxService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/measure-boxes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(measureBox: IMeasureBox): Observable<EntityResponseType> {
    return this.http.post<IMeasureBox>(this.resourceUrl, measureBox, { observe: 'response' });
  }

  update(measureBox: IMeasureBox): Observable<EntityResponseType> {
    return this.http.put<IMeasureBox>(`${this.resourceUrl}/${getMeasureBoxIdentifier(measureBox) as number}`, measureBox, {
      observe: 'response',
    });
  }

  partialUpdate(measureBox: IMeasureBox): Observable<EntityResponseType> {
    return this.http.patch<IMeasureBox>(`${this.resourceUrl}/${getMeasureBoxIdentifier(measureBox) as number}`, measureBox, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMeasureBox>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMeasureBox[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMeasureBoxToCollectionIfMissing(
    measureBoxCollection: IMeasureBox[],
    ...measureBoxesToCheck: (IMeasureBox | null | undefined)[]
  ): IMeasureBox[] {
    const measureBoxes: IMeasureBox[] = measureBoxesToCheck.filter(isPresent);
    if (measureBoxes.length > 0) {
      const measureBoxCollectionIdentifiers = measureBoxCollection.map(measureBoxItem => getMeasureBoxIdentifier(measureBoxItem)!);
      const measureBoxesToAdd = measureBoxes.filter(measureBoxItem => {
        const measureBoxIdentifier = getMeasureBoxIdentifier(measureBoxItem);
        if (measureBoxIdentifier == null || measureBoxCollectionIdentifiers.includes(measureBoxIdentifier)) {
          return false;
        }
        measureBoxCollectionIdentifiers.push(measureBoxIdentifier);
        return true;
      });
      return [...measureBoxesToAdd, ...measureBoxCollection];
    }
    return measureBoxCollection;
  }
}
