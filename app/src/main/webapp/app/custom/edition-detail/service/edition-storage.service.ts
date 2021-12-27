import { Injectable } from '@angular/core';
import {IEdition} from "../edition-detail.model";

@Injectable({ providedIn: 'root' })
export class StorageService {

  public activeEdition: IEdition | undefined;
  public activeProjectId: number | boolean = false;

  public getActiveProjectId(): number | boolean {
    return this.activeProjectId;
  }

  public setActiveProjectID(projectId: number): void {
    this.activeProjectId = projectId;
  }
  public getActiveEdition() :IEdition | undefined {
      return this.activeEdition;
  }
  public setActiveEdition(e: IEdition): void {
     this.activeEdition = e;
  }
}
