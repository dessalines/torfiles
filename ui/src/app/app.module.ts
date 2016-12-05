import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { UploadComponent } from './upload/upload.component';

import { AppRoutingModule }     from './app-routing.module';
import { SearchComponent } from './search/search.component';

import { PaginationModule } from 'ng2-bootstrap/ng2-bootstrap';
import { FileUploadModule } from 'ng2-file-upload/ng2-file-upload';
import { FileSizePipe } from './pipes/file-size.pipe';
import { MomentPipe } from './pipes/moment.pipe';
import { TorrentDetailComponent } from './torrent-detail/torrent-detail.component';
import {SearchService} from './search/search.service';
import { TorrentDetailService} from './torrent-detail.service';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    FooterComponent,
    SearchComponent,
    UploadComponent,
    FileSizePipe,
    MomentPipe,
    TorrentDetailComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    PaginationModule,
    FileUploadModule
  ],
  providers: [SearchService, TorrentDetailService],
  bootstrap: [AppComponent]
})
export class AppModule { }
