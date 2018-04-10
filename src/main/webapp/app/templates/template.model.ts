import { Repository } from './repository.model';

export class Template {
    type: string;
    title: string;
    description: string;
    categories: string[];
    platform: string;
    logo: string;
    image: string;
    ports: string[];
    volumes: string[];
    note: string;
    repository: Repository;
}
