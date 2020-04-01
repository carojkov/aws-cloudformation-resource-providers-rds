package software.amazon.rds.dbsubnetgroup;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.OperationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<RdsClient> proxyRdsClient;

    @Mock
    RdsClient rds;

    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        handler = new CreateHandler();
        rds = mock(RdsClient.class);
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyRdsClient = new ProxyClient<RdsClient>() {
            @Override
            public <RequestT extends AwsRequest, ResponseT extends AwsResponse>
            ResponseT
            injectCredentialsAndInvokeV2(RequestT request, Function<RequestT, ResponseT> requestFunction) {
                return proxy.injectCredentialsAndInvokeV2(request, requestFunction);
            }

            @Override
            public <RequestT extends AwsRequest, ResponseT extends AwsResponse>
            CompletableFuture<ResponseT>
            injectCredentialsAndInvokeV2Aync(RequestT request,
                                             Function<RequestT, CompletableFuture<ResponseT>> requestFunction) {
                throw new UnsupportedOperationException();
            }

            @Override
            public RdsClient client() {
                return rds;
            }
        };
    }

    @AfterEach
    public void post_execute() {
        verifyNoMoreInteractions(proxyRdsClient.client());
    }

    @Test
    public void handleRequest_SimpleSuccess() {

        final CreateDbSubnetGroupResponse createDbSubnetGroupResponse = CreateDbSubnetGroupResponse.builder().build();
        when(proxyRdsClient.client().createDBSubnetGroup(any(CreateDbSubnetGroupRequest.class))).thenReturn(createDbSubnetGroupResponse);

        final DescribeDbSubnetGroupsResponse describeCreatingDbSubnetGroupsResponse = DescribeDbSubnetGroupsResponse.builder().dbSubnetGroups(DB_SUBNET_GROUP_CREATING).build();
        final DescribeDbSubnetGroupsResponse describeActiveDbSubnetGroupsResponse = DescribeDbSubnetGroupsResponse.builder().dbSubnetGroups(DB_SUBNET_GROUP_ACTIVE).build();

        AtomicInteger attempt = new AtomicInteger(2);
        when(proxyRdsClient.client().describeDBSubnetGroups(any(DescribeDbSubnetGroupsRequest.class))).then((m) -> {
            switch (attempt.getAndDecrement()) {
                case 2:
                    return describeCreatingDbSubnetGroupsResponse;
                default:
                    return describeActiveDbSubnetGroupsResponse;
            }
        });
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().build();
        when(proxyRdsClient.client().listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(listTagsForResourceResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .logicalResourceIdentifier("dbsubnet")
                .clientRequestToken("4b90a7e4-b791-4512-a137-0cf12a23451e")
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyRdsClient, logger);


        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyRdsClient.client()).createDBSubnetGroup(any(CreateDbSubnetGroupRequest.class));
        verify(proxyRdsClient.client(), times(3)).describeDBSubnetGroups(any(DescribeDbSubnetGroupsRequest.class));
        verify(proxyRdsClient.client()).listTagsForResource(any(ListTagsForResourceRequest.class));
    }


    @Test
    public void handleRequest_SimpleSuccessAlternative() {

        final CreateDbSubnetGroupResponse createDbSubnetGroupResponse = CreateDbSubnetGroupResponse.builder().build();
        when(proxyRdsClient.client().createDBSubnetGroup(any(CreateDbSubnetGroupRequest.class))).thenReturn(createDbSubnetGroupResponse);

        final DescribeDbSubnetGroupsResponse describeCreatingDbSubnetGroupsResponse = DescribeDbSubnetGroupsResponse.builder().dbSubnetGroups(DB_SUBNET_GROUP_CREATING).build();
        final DescribeDbSubnetGroupsResponse describeActiveDbSubnetGroupsResponse = DescribeDbSubnetGroupsResponse.builder().dbSubnetGroups(DB_SUBNET_GROUP_ACTIVE).build();

        AtomicInteger attempt = new AtomicInteger(2);
        when(proxyRdsClient.client().describeDBSubnetGroups(any(DescribeDbSubnetGroupsRequest.class))).then((m) -> {
            switch (attempt.getAndDecrement()) {
                case 2:
                    return describeCreatingDbSubnetGroupsResponse;
                default:
                    return describeActiveDbSubnetGroupsResponse;
            }
        });
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().build();
        when(proxyRdsClient.client().listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(listTagsForResourceResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL_ALTERNATIVE)
                .logicalResourceIdentifier("dbsubnet")
                .clientRequestToken("4b90a7e4-b791-4512-a137-0cf12a23451e")
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyRdsClient, logger);


        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        verify(proxyRdsClient.client()).createDBSubnetGroup(any(CreateDbSubnetGroupRequest.class));
        verify(proxyRdsClient.client(), times(3)).describeDBSubnetGroups(any(DescribeDbSubnetGroupsRequest.class));
        verify(proxyRdsClient.client()).listTagsForResource(any(ListTagsForResourceRequest.class));
    }
}