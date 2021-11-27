import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITag } from '../tag.model';
import { TagService } from '../service/tag.service';
import { TagDeleteDialogComponent } from '../delete/tag-delete-dialog.component';

@Component({
  selector: 'jhi-tag',
  templateUrl: './tag.component.html',
})
export class TagComponent implements OnInit {
  tags?: ITag[];
  isLoading = false;

  constructor(protected tagService: TagService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tagService.query().subscribe(
      (res: HttpResponse<ITag[]>) => {
        this.isLoading = false;
        this.tags = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITag): number {
    return item.id!;
  }

  delete(tag: ITag): void {
    const modalRef = this.modalService.open(TagDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tag = tag;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
