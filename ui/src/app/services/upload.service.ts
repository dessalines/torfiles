import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { Http, Response } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';
import { environment } from '../../environments/environment';

@Injectable()
export class UploadService {

	private uploadMagnetUrl(): string {
		return environment.endpoint + 'upload_magnet_links';
	}

	constructor(private http: Http) { }

	uploadMagnetLinks(magnetTextArea: string): Observable<any> {

		return this.http.post(this.uploadMagnetUrl(), magnetTextArea)
			.map(r => r.json())
			.catch(this.handleError);
	}

	private handleError(error: any) {
		// We'd also dig deeper into the error to get a better message
		let errMsg = error._body;

		return Observable.throw(errMsg);
	}

}
