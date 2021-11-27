import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEdition } from '../edition.model';
import { EditionService } from '../service/edition.service';

@Component({
  templateUrl: './edition-delete-dialog.component.html',
})
export class EditionDeleteDialogComponent {
  edition?: IEdition;

  constructor(protected editionService: EditionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.editionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
