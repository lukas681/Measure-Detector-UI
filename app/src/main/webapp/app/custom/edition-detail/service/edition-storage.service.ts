import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StorageService {

  public activeProjectId: number | boolean = false;


  public getActiveProjectId(): number | boolean {
    return this.activeProjectId;
  }

  public setActiveProjectID(projectId: number): void {
    this.activeProjectId = projectId;
  }
}
