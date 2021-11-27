import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PageComponent } from './list/page.component';
import { PageDetailComponent } from './detail/page-detail.component';
import { PageUpdateComponent } from './update/page-update.component';
import { PageDeleteDialogComponent } from './delete/page-delete-dialog.component';
import { PageRoutingModule } from './route/page-routing.module';

@NgModule({
  imports: [SharedModule, PageRoutingModule],
  declarations: [PageComponent, PageDetailComponent, PageUpdateComponent, PageDeleteDialogComponent],
  entryComponents: [PageDeleteDialogComponent],
})
export class PageModule {}
