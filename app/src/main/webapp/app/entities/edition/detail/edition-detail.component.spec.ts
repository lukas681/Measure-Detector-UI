import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EditionDetailComponent } from './edition-detail.component';

describe('Edition Management Detail Component', () => {
  let comp: EditionDetailComponent;
  let fixture: ComponentFixture<EditionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ edition: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EditionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EditionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load edition on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.edition).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
