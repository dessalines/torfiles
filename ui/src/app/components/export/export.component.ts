import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-export',
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.scss']
})
export class ExportComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  jsonDump(): string {
  	return environment.endpoint + 'torshare.json';
  }

  pgDump(): string {
  	return environment.endpoint + 'torshare.pgdump';
  }

}
