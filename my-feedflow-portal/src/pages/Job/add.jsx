import React, { Component } from 'react';
import JobForm from './components/Form';
import { withRouter } from 'react-router';
import { DatabaseService,JobTypeService,EmailService,JobStepService,JobService } from '../../service';

@withRouter
export default class add extends Component {
    static displayName = 'add';

    constructor(props) {
        super(props);
        this.state = {
            database : [],
            jobSteps : [],
            email : [],
            jobType : [],
        };
    }

    componentDidMount = async() => {
        let database = await DatabaseService.list()
        let jobSteps = await JobTypeService.list()
        let email = await EmailService.list()
        let jobType = await JobTypeService.list()
        this.setState({database,jobSteps,email,jobType})
    }

    onSubmit = async(data) => {
        await JobService.insert(data);
        this.props.history.push('/job')
        return true
    }

    checkScheduled = async(value) => {
        let data = await JobService.checkScheduled(value);
        if(data){return null} else {return "this scheduled not crontab"}
    }

    loadStepConfig = async (value) =>{
        let data = await JobTypeService.getConfig(value);
        return data || []
    }

    render() {
        return (
            <div>
                <JobForm email={this.state.email} jobType={this.state.jobType} url={this.state.url} database={this.state.database} jobSteps={this.state.jobSteps} checkScheduled={this.checkScheduled} loadStepConfig={this.loadStepConfig} onSubmit={this.onSubmit}/>
            </div>
        );
    }
}