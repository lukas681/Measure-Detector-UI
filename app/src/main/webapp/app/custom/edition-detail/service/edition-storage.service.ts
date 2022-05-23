import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StorageService {

  public activeEditionId: number | boolean = false;
  public activeProjectId: number | boolean = false;

  public getActiveProjectId(): number | boolean {
    return this.activeProjectId;
  }

  public setActiveProjectID(projectId: number): void {
    this.activeProjectId = projectId;
  }
  public getActiveEditionId() :number | boolean | undefined {
      return this.activeEditionId;
  }
  public setActiveEditionId(e: number | boolean): void {
     this.activeEditionId = e;
  }
}
