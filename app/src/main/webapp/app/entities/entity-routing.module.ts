import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'project',
        data: { pageTitle: 'measureDetectorApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'edition',
        data: { pageTitle: 'measureDetectorApp.edition.home.title' },
        loadChildren: () => import('./edition/edition.module').then(m => m.EditionModule),
      },
      {
        path: 'page',
        data: { pageTitle: 'measureDetectorApp.page.home.title' },
        loadChildren: () => import('./page/page.module').then(m => m.PageModule),
      },
      {
        path: 'measure-box',
        data: { pageTitle: 'measureDetectorApp.measureBox.home.title' },
        loadChildren: () => import('./measure-box/measure-box.module').then(m => m.MeasureBoxModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'measureDetectorApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
