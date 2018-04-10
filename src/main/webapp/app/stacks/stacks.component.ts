import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../app.constants';
import { Stack } from './stack.model';
import { Template } from '../templates/template.model';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/forkJoin';
import { Repository } from '../templates/repository.model';

@Component({
    selector: 'jhi-templates',
    templateUrl: './stacks.component.html'
})
export class StacksComponent implements OnInit, OnDestroy {
    stacks: Stack[];
    selectedStack: Stack;

    constructor(private http: HttpClient) {
    }

    ngOnInit(): void {
        const stacksObservable = this.http.get<Stack[]>(SERVER_API_URL + 'api/docker/stacks');
        const templatesObservable = this.http.get<Template[]>(SERVER_API_URL + 'api/templates');
        Observable.forkJoin([stacksObservable, templatesObservable]).subscribe((results) => {
            const stacks = results[0];
            const templates = results[1];
            this.stacks = stacks.map((stack: Stack) => {
                const template = this.findTemplateByRepository(templates, stack.template.repository);
                if (template) {
                    stack.template = template;
                }
                return stack;
            });
            console.log(this.stacks);
        });
    }
    ngOnDestroy(): void {
    }

    selectStack(stack: Stack) {
        this.selectedStack = stack;
    }

    clearSelectedStack() {
        this.selectedStack = null;
    }

    private findTemplateByRepository(templates: Template[], repository: Repository): Template {
        return templates.find((template) =>
            template.repository
            && repository.url === template.repository.url
            && repository.stackfile === template.repository.stackfile
        );
    }
}
