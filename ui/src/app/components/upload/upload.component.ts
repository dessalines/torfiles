import { Component, OnInit } from '@angular/core';
import { FileSelectDirective, FileDropDirective, FileUploader } from 'ng2-file-upload/ng2-file-upload';
import { environment } from '../../../environments/environment';
import { UploadService } from '../../services';

const URL = environment.endpoint + 'upload';

@Component({
	selector: 'app-upload',
	templateUrl: './upload.component.html',
	styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit {

	public magnetTextArea: string;
	public magnetUploadMessage: string;
	public magnetUploading: boolean = false;

	public uploader: FileUploader = new FileUploader({ url: URL });
	public hasBaseDropZoneOver: boolean = false;

	public fileOverBase(e: any): void {
		this.hasBaseDropZoneOver = e;
	}

	constructor(private uploadService: UploadService) { }

	ngOnInit() {
		this.uploader.onAfterAddingFile = (file) => { file.withCredentials = false; };
		this.uploader.onCompleteItem = (file => {
		});
	}

	uploadMagnets() {

		console.log(this.magnetTextArea);

		this.magnetUploading = true;
		this.uploadService.uploadMagnetLinks(this.magnetTextArea).subscribe(
			d => {
				this.magnetUploadMessage = d.message;
				this.magnetUploading = false;
			}, error => {
				this.magnetUploadMessage = error;
				this.magnetUploading = false;
			});
	}





}
