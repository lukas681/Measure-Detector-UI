import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'myprojects',
        data: { pageTitle: 'measureDetectorApp.project.home.title' }, // TODO ADD TITLE
        loadChildren: () => import('./myprojects/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'edition-detail',
        data: { pageTitle: 'measureDetectorApp.project.home.title' }, // TODO ADD TITLE
        loadChildren: () => import('./edition-detail/edition-detail.module').then(m => m.EditionDetailModule),
      },
      {
        path: 'editing-detail',
        data: { pageTitle: 'measureDetectorApp.project.home.title' }, // TODO ADD TITLE
        loadChildren: () => import('./editing/editing.module').then(m => m.EditingModule),
      }
    ]),
  ],
})
export class CustomRoutingModule {}
