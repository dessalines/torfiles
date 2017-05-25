import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import {
  NavbarComponent,
  FooterComponent,
  UploadComponent,
  TorrentDetailComponent,
  SearchComponent,
  ExportComponent
} from './components';

import {
  MomentPipe,
  FileSizePipe
} from './pipes';

import {
  SearchService,
  TorrentDetailService,
  UploadService
} from './services';

import { AppRoutingModule } from './app-routing.module';
import {
  PaginationModule,
  TooltipModule
} from 'ngx-bootstrap';
import { FileUploadModule } from 'ng2-file-upload/ng2-file-upload';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    SearchComponent,
    UploadComponent,
    FileSizePipe,
    MomentPipe,
    TorrentDetailComponent,
    ExportComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    PaginationModule.forRoot(),
    TooltipModule.forRoot(),
    FileUploadModule
  ],
  providers: [SearchService, TorrentDetailService, UploadService],
  bootstrap: [AppComponent]
})
export class AppModule { }
