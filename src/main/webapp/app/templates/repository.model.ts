export class Repository {
    url: string;
    stackfile: string;

    public equals(obj: Repository) : boolean {
        return this.url === obj.url && this.stackfile === obj.stackfile;
    }
}
