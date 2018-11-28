import React, { Component } from 'react';
import { Table, Pagination, Dialog, Feedback, Button, Search } from '@icedesign/base';
import IceContainer from '@icedesign/container';
// import IceLabel from '@icedesign/label';
import PropTypes from 'prop-types';

export default class JobTable extends Component {
    static displayName = 'JobTable';

    static propTypes = {
        list : PropTypes.func.isRequired,
        run : PropTypes.func.isRequired,
        del : PropTypes.func.isRequired,
        edit : PropTypes.func.isRequired,
        goAdd : PropTypes.func.isRequired,
    };

    static defaultProps = {};

    constructor(props) {
        super(props);
        this.state = {
          loading : true,
          data : [],
          currentPage : 1,
          pageSize : 10,
          total : 0,
        };
    }

    componentDidMount() {
        this.fetchData(1);
    }

    fetchData = async() => {
        this.setState({loading : true})
        let data = await this.props.list()
        this.setState({
          data : data,
          loading : false,
          pageData : data.slice(0, 10),
          pageSize : 10,
          currentPage : 1,
          total : data.length
        })
    };

    onSearch = (searchObj) => {
        let keyword = searchObj.key;
        if(!keyword) {
          this.refresh();
        } else {
          keyword = keyword.toLowerCase();
          this.refresh((job, index, self) => {
            for(let attr in job) {
              if(job[attr] && job[attr].toString().toLowerCase().indexOf(keyword) != -1) {
                return true;
              }
            }
            return false;
          });
        }
    }

    render() {
        return (
            <div className="simple-table">
                <div style={{textAlign: 'right', paddingLeft:'20px', paddingTop:'10px', paddingRight:'50px', width:'100%', backgroundColor:'#fff'}}>
                <Search size="large" style={{display:'inline-block', lineHeight:'30px', float:'left'}}
                    onSearch={this.onSearch.bind(this)} placeholder="please input to search..." />
                <Button type="primary" size="large" style={{minWidth:'100px'}} onClick={this.props.goAdd}>Add New Job</Button>
                <div style={{clear: 'both'}}/>
                </div>
                <IceContainer>
                <Table dataSource={this.state.pageData} isLoading={this.state.loading} className="basic-table" hasBorder={false} locale={{empty:"no data"}}>
                    <Table.Column title="Name" dataIndex="name" width={150}/>
                    <Table.Column title="Scheduled" dataIndex="scheduled" width={85} />
                    <Table.Column title="Job Type" dataIndex="jobTypeValue.name" width={85}/>
                    <Table.Column title="Status" dataIndex="status" width={85} cell={this.renderStatus}/>
                    <Table.Column title="Options" dataIndex="operation" width={150} cell={this.renderOperations}/>
                </Table>
                <div style={{ textAlign: 'right',paddingTop: '26px',}}>
                    <Pagination current={this.state.currentPage} pageSize={this.state.pageSize} total={this.state.total} onChange={this.changePage} type={'normal'} locale={{next:'Next',prev:'Prev'}} />
                </div>
                </IceContainer>
            </div>
        );
    }
}