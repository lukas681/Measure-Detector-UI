import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EditionComponent } from './list/edition.component';
import { EditionDetailComponent } from './detail/edition-detail.component';
import { EditionUpdateComponent } from './update/edition-update.component';
import { EditionDeleteDialogComponent } from './delete/edition-delete-dialog.component';
import { EditionRoutingModule } from './route/edition-routing.module';

@NgModule({
  imports: [SharedModule, EditionRoutingModule],
  declarations: [EditionComponent, EditionDetailComponent, EditionUpdateComponent, EditionDeleteDialogComponent],
  entryComponents: [EditionDeleteDialogComponent],
})
export class EditionDetailModule {}
