import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PageDetailComponent } from './page-detail.component';

describe('Page Management Detail Component', () => {
  let comp: PageDetailComponent;
  let fixture: ComponentFixture<PageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ page: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load page on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.page).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
