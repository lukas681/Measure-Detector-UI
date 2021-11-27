import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPage } from '../page.model';
import { PageService } from '../service/page.service';

@Component({
  templateUrl: './page-delete-dialog.component.html',
})
export class PageDeleteDialogComponent {
  page?: IPage;

  constructor(protected pageService: PageService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
