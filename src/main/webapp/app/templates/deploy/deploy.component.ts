import { Component, Input, OnDestroy, OnInit, EventEmitter, Output, OnChanges } from '@angular/core';
import { Template } from '../template.model';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

@Component({
  selector: 'jhi-deploy',
  templateUrl: './deploy.component.html',
  styles: []
})
export class DeployComponent implements OnInit, OnDestroy, OnChanges {
    @Input() template: Template;
    @Output() cancel = new EventEmitter();
    stack: any;

    constructor(private http: HttpClient) {}

    ngOnInit() {
    }

    ngOnDestroy() {
    }

    ngOnChanges() {
        this.stack = {
            environment: {},
            template: this.template,
            name: '',
        };
        console.log(this.template);
    }

    cancelDeploy() {
        this.cancel.emit();
    }

    doDeploy() {
        this.http.post(SERVER_API_URL + 'api/docker/stacks', this.stack)
            .subscribe((stack) => {
            console.log('stack');
        });
    }
}
