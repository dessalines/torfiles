import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { TorrentDetailService } from '../../services';
import { Tools } from '../../shared';
import { environment } from '../../../environments/environment';


@Component({
  selector: 'app-torrent-detail',
  templateUrl: './torrent-detail.component.html',
  styleUrls: ['./torrent-detail.component.scss']
})
export class TorrentDetailComponent implements OnInit {

  public d: any;

  constructor(private torrentDetailService: TorrentDetailService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer) { }

  ngOnInit() {
    let infoHash = this.route.snapshot.params['info_hash'];
    this.torrentDetailService.getTorrentDetails(infoHash).subscribe(details => {
      this.d = details;
      console.log(this.d);
    });
  }

  public generateMagnetLink(name, infoHash, index = null) {
    return Tools.generateMagnetLink(name, infoHash, index);
  }

}
