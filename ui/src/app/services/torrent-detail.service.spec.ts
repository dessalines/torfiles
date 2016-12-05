/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TorrentDetailService } from './torrent-detail.service';

describe('Service: TorrentDetail', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TorrentDetailService]
    });
  });

  it('should ...', inject([TorrentDetailService], (service: TorrentDetailService) => {
    expect(service).toBeTruthy();
  }));
});
