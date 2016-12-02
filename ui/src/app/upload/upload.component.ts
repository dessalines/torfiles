import { Component, OnInit } from '@angular/core';
import { FileSelectDirective, FileDropDirective, FileUploader } from 'ng2-file-upload/ng2-file-upload';
import { environment } from '../../environments/environment';

const URL = environment.endpoint + 'upload';

@Component({
	selector: 'app-upload',
	templateUrl: './upload.component.html',
	styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit {

	public uploader: FileUploader = new FileUploader({ url: URL });
	public hasBaseDropZoneOver: boolean = false;

	public fileOverBase(e: any): void {
		this.hasBaseDropZoneOver = e;
	}

	constructor() { }

	ngOnInit() {
		this.uploader.onAfterAddingFile = (file)=> { file.withCredentials = false; };
	}



}
